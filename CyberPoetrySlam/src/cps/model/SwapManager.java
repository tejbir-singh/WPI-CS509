package cps.model;

import java.util.ArrayList;
import java.util.Random;

import cps.view.ApplicationPanel;
import broker.BrokerClient;
import broker.handler.IHandleBrokerMessage;
import broker.handler.ReaderThread;
import broker.util.ConfirmSwapMessage;
import broker.util.IProtocol;
import broker.util.MatchSwapMessage;
import broker.util.Swap;

/**
 * @author Devin
 *
 */
public class SwapManager implements IHandleBrokerMessage {
	static SwapManager instance;
	ArrayList<Word> words;
	GameManager gm;
	String[] requestWords;
	String[] requestTypes;
	BrokerClient broker;
	ApplicationPanel panel;
	
	/** Thread managing connections. When you are done, call shutdown for clean exit. */
	ReaderThread thread;
	
	/** Determines connectivity. */
	boolean connected;
	
	/**
	 * Will need to be changed once we decide where broker is being run.
	 * 
	 * In general, information such as this shouldn't be in the compiled Java file 
	 */
	public final static String brokerHost = "gheineman.cs.wpi.edu";
	public final static int    brokerPort = 9172;
	
	/** 
	 * Constructor.
	 */
	private SwapManager() {
		this.words = new ArrayList<Word>();
		this.gm = GameManager.getInstance();
	}
	
	public static SwapManager getInstance() {
		if (instance == null) {
			instance = new SwapManager();
		}
		return instance;
	}
	
	/**
	 * Add a Word to the SwapManager.
	 * @param w Word to add
	 */
	public void add(Entity e) {
		if (e instanceof Word){
			words.add((Word) e);
		} else{
			for(Row row : ((Poem) e).getRows()){
				for(Word word : row.getWords()){
					words.add(word);
				}	
			}
		}
		if (!words.isEmpty()) {
			panel.getSwapButton().setEnabled(true);
		}
	}
	
	/**
	 * Remove a Word from the SwapManager.
	 * @param w Word to remove
	 */
	public void remove(Word w) {
		words.remove(w);
		if (words.isEmpty()) {
			panel.getSwapButton().setEnabled(false);
		}
	}
	
	/** Are we connected to the broker? */
	public boolean isConnected() { return connected; }
	
	/** Get our unique id. */
	public String getID() { 
		if (broker == null) { return null; }
		return broker.getID(); 
	}
	
	/** Cleanly shutdown interface to broker. */
	public void shutdown() { 
		if (thread != null) { thread.shutdown(); }
	}
	
	/** Try to connect to broker on default host. */
	public boolean connect() {
		return connect(brokerHost);
	}
	
	/**
	 * Try to connect to the broker.
	 * 
	 * @param host 
	 */
	public boolean connect(String host) {
		
		broker = new BrokerClient(host, brokerPort);

		if (!broker.connect()) {
			System.err.println("unable to connect to broker on " + host);
			broker.shutdown();
			return false;
		}
		
		// at this point we are connected, and we will block waiting for 
		// any messages from the broker. These will be sent to the process
		System.out.println("Connected as : " + broker.getID() + " : " + broker.getStatus());
		
		// start thread to process commands from broker.
		thread = new ReaderThread(broker, this);
		thread.start();
		
		// nothing more to do here. This thread is now independent and will respond
		// to broker messages, as well as our own concerns.
		return true;
	}
	
	/**
	 * Sends swap request to the broker
	 */
	public void sendMessage(String msg) {
		broker.getBrokerOutput().println(msg);
	}
	
	/**
	 * Note: Your code will have to provide a suitable implementation that integrates
	 * with your individual board.
	 * 
	 * Here is where all broker messages are received. You must check each one, and
	 * act accordingly.
	 */
	@Override
	public void process(BrokerClient broker, String msg) {
		System.out.println("Process message:" + msg);
		
		if (msg.startsWith(IProtocol.denySwapMsg)) {
			System.out.println("Denied swap request");
			panel.getSwapButton().setEnabled(true);
			return;
		}
		
		// some third party has asked for a swap. Pull this out into its own controller
		if (msg.startsWith(IProtocol.matchSwapMsg)) {
			Random r = new Random();
			Swap s = MatchSwapMessage.getSwap(msg);
			System.out.println("Third party trying a swap:" + s.flatten());
			
			// swap is requesting words. We have to check to see if we have these
			// requested words in the UnprotectedArea.
			ArrayList<Word> matched = new ArrayList<Word>();
			
			boolean failed = true;
			for (int i = 0; i < s.requestWords.length; i++) {
				failed = true;
				if (s.requestWords[i].equals("*")) {
					// use a random word
					while (true) {
						Word w = gm.getUa().getWords().get(r.nextInt(gm.getUa().getWords().size()));
						if (!matched.contains(w) 
								&& (w.getType().toString().toLowerCase().equals(s.requestTypes[i].toLowerCase()) 
										|| s.requestTypes[i].equals("*"))) {
							matched.add(w);
							break;
						}
					}
					failed = false;
				}
				for (Word w : gm.getUa().getWords()) {
					if (w.getValue().equals(s.requestWords[i]) 
							&& (w.getType().toString().toLowerCase().equals(s.requestTypes[i].toLowerCase()) 
									|| s.requestTypes[i].equals("*"))) {
						matched.add(w);
						failed = false;
						break;
					}
				}
				
				if (failed) {
					System.out.println("Unable to satisfy swap request");
					broker.getBrokerOutput().println(IProtocol.denySwapMsg + 
							IProtocol.separator + s.requestor_id);
					return;
				}
			}
			
			System.out.println("Accepting satisfy swap request");
			
			s.acceptor_id = broker.getID();
			for (int i = 0; i < matched.size(); i++) {
				Word w = matched.get(i);
				s.requestWords[i] = w.getValue();
				s.requestTypes[i] = w.getType().toString().toLowerCase();
			}
			
			broker.getBrokerOutput().println(IProtocol.confirmSwapMsg + IProtocol.separator + s.flatten());
			return;
		}
		
		// Execute the swap
		if (msg.startsWith(IProtocol.confirmSwapMsg)) {
			Swap s = ConfirmSwapMessage.getSwap(msg);
			
			// carry out the swap. We were the one making the request...
			String wordsToRemove[], wordsToAdd[], wordTypesToAdd[];
			if (broker.getID().equals(s.requestor_id)) {
				wordsToRemove = s.offerWords;
				wordsToAdd = s.requestWords;
				wordTypesToAdd = s.requestTypes;
			} else {
				wordsToRemove = s.requestWords;
				wordsToAdd = s.offerWords;
				wordTypesToAdd = s.offerTypes;
			}

			// notify client of swap
			String output = "SWAPPING ";
			for (String str : wordsToRemove) {
				output += str + " ";
			}
			output += "FOR ";
			for (String str : wordsToAdd) {
				output += str + " ";
			}
			System.out.println(output);
			
			// remove each word as found
			boolean found = false;
			for (int i = 0; i < s.n; i++) {
				for (Word w : this.words) {
					if (w.getValue().equals(wordsToRemove[i])) {
						words.remove(w);
						found = true;
						break;
					}
				}
				if (!found) {
					for (Word w : gm.getUa().getWords()) {
						if (w.getValue().equals(wordsToRemove[i])) {
							gm.getUa().remove(w);
							break;
						}
					}
				}
			}
				
			for (int i = 0; i < s.n; i++) {
				int rx = (int) Math.round(Math.random() * 600);
				int ry = (int) Math.round((Math.random() * 200) + GameManager.AREA_DIVIDER);
				
				Type type = Type.noun;					// default
				for (Type t : Type.values()) {
					if (t.toString().equals(wordTypesToAdd[i])) {
						type = t;
						break;
					}
				}
				Word w = new Word (rx, ry, wordsToAdd[i].length() * 15, 15, type, wordsToAdd[i]);
				gm.getUa().add(w);
			}
			
			// refresh viewing area
			panel.validateUndo(false);
			panel.validateRedo(false);
			panel.redraw();
			panel.repaint();
		}
	}
	
	/**
	 * Format the request to swap for the Broker.
	 * @return true if successful
	 */
	public String formatSwapRequest() {
		// for each Word, get the value and type
		String[] values = new String[words.size()];
		String[] types = new String[words.size()];
		
		for (int i = 0; i < values.length; i++) {
			values[i] = words.get(i).getValue();
			types[i] = words.get(i).getType().toString().toLowerCase().trim();
		}
		
		Swap s = new Swap(broker.getID(), "*", values.length, types, values, requestTypes, requestWords);
		
		// Generate swap request
		return s.flatten();
	}

	/**
	 * If Broker vanishes, then we disable buttons. Note there is no logic to
	 * try to reconnect to broker.
	 */
	@Override
	public void brokerGone() {
		System.err.println("Lost broker connection.");
		panel.getSwapButton().setEnabled(false);
	}
	
	public ArrayList<Word> getWords() {
		return this.words;
	}

	public Word getWord(int x, int y) {
		for (Word w : words) {
			// create a dummy; remove this later
			Word tmp = new Word(x, y, 0, 0, null, null);
			if (w.intersect(tmp)) {
				return w;
			}
		}
		return null;
	}
	
	public void setRequestWords(String[] str) {
		this.requestWords = str;
	}
	
	public void setRequestTypes(String[] str) {
		this.requestTypes = str;
	}

	public void setAppPanel(ApplicationPanel panel) {
		this.panel = panel;
	}
}
