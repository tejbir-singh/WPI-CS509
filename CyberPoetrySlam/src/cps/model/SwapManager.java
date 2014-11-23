package cps.model;

import java.util.ArrayList;

import broker.BrokerClient;
import broker.handler.IHandleBrokerMessage;
import broker.handler.ReaderThread;
import broker.util.ConfirmSwapMessage;
import broker.util.IProtocol;
import broker.util.MatchSwapMessage;
import broker.util.Swap;

public class SwapManager implements IHandleBrokerMessage {
	private static SwapManager instance;
	private ArrayList<Word> words;
	private UnprotectedArea ua;
	private ArrayList<String> requestWords;
	private ArrayList<Type> requestTypes;
	private BrokerClient broker;
	
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
		words = new ArrayList<Word>();
		requestWords = new ArrayList<String>();
		ua = UnprotectedArea.getInstance();
	}
	
	/**
	 * Singleton pattern implementation.
	 * @return the instance of SwapManager
	 */
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
	}
	
	/**
	 * Remove a Word from the SwapManager.
	 * @param w Word to remove
	 */
	public void remove(Word w) {
		words.remove(w);
	}
	
	/**
	 * Send the request to swap with the Broker
	 * @return true if successful
	 */
	public boolean sendSwapRequest() {
		// for each Word, get the value and type
		String request = "";
		String[] values = new String[words.size()];
		String[] types = new String[words.size()];
		String[] requestTypes = new String[words.size()];
		for (int i = 0; i < values.length; i++) {
			values[i] = words.get(i).getValue();
			types[i] = words.get(i).getType().toString().toLowerCase();
			requestTypes[i] = this.requestTypes.get(i).toString().toLowerCase();
		}
		
		// Generate swap request
		request.concat("" + values.length + ":");
		for (String t : types) {
			request.concat("" + t + ":");
		}
		for (String v : values) {
			request.concat("" + v + ":");
		}
		for (String rt : requestTypes) {
			request.concat("" + rt + ":");
		}
		for (String rw : requestWords) {
			request.concat("" + rw + ":");
		}
		request.substring(0, request.length()-1);
		return true;
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
			return;
		}
		
		// some third party has asked for a swap. Pull this out into its own controller
		if (msg.startsWith(IProtocol.matchSwapMsg)) {
			Swap s = MatchSwapMessage.getSwap(msg);
			System.out.println("Third party trying a swap:" + s.flatten());
			
			// swap is requesting words. We have to check to see if we have these
			// requested words in the UnprotectedArea.
			ArrayList<Word> matched = new ArrayList<Word>();
			
			boolean failed = true;
			for (int i = 0; i < s.requestWords.length; i++) {
				failed = true;
				for (Word w : ua.getWords()) {
					if (w.getValue().equals(s.requestWords[i])) {
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
			
			// what should we do? Agree of course! Here is where your code would
			// normally "convert" wildcards into actual words in your board state.
			// for now this is already assumed (note sample/other swap)
			broker.getBrokerOutput().println(IProtocol.confirmSwapMsg + IProtocol.separator + s.flatten());
			return;
		}
		
		// Execute the swap
		if (msg.startsWith(IProtocol.confirmSwapMsg)) {
			Swap s = ConfirmSwapMessage.getSwap(msg);
			
			System.out.println("Before Swap:");
			
			// carry out the swap. We were the one making the request...
			String wordsToRemove[];
			String wordsToAdd[];
			if (broker.getID().equals(s.requestor_id)) {
				wordsToRemove = s.offerWords;
				wordsToAdd = s.requestWords;
			} else {
				wordsToRemove = s.requestWords;
				wordsToAdd = s.offerWords;
			}

			// remove each word as found
			for (int i = 0; i < s.n; i++) {
				for (Word w : ua.getWords()) {
					if (w.getValue().equals(wordsToRemove[i])) {
						ua.remove(w);
						break;
					}
				}
			}
				
			// now we add new shapes. (at least 40 pixels inwards on all sides)
			// TODO: Update JTable, change number to constant
			for (int i = 0; i < s.n; i++) {
				int rx = (int) Math.round(Math.random() * 600);
				int ry = (int) Math.round((Math.random() * 100) + GameManager.AREA_DIVIDER);
				
				// TODO: Get Word type
				Word w = new Word (rx, ry, words.get(i).getValue().length() * 15, 15, Type.ADJECTIVE, wordsToAdd[i]);
				ua.add(w);
			}
			
			// TODO: must refresh viewing area
			// gui.getWordPanel().redraw();
			// gui.getWordPanel().repaint();
				
			return;
		}
	}

	/**
	 * If Broker vanishes, then we disable buttons. Note there is no logic to
	 * try to reconnect to broker.
	 */
	@Override
	public void brokerGone() {
		System.err.println("Lost broker connection.");
		// gui.getSwapOtherSampButton().setEnabled(false);
		// gui.getSwapSampOtherButton().setEnabled(false);
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
}
