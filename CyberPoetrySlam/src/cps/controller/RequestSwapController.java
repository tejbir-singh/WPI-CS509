package cps.controller;

import java.awt.event.MouseAdapter;

import broker.util.IProtocol;
import cps.model.GameManager;
import cps.model.SwapManager;
import cps.view.ApplicationPanel;

/**
 * This class communicates with the SwapManager and submits a swap request to the broker.
 * @author Devin
 */
public class RequestSwapController extends MouseAdapter {
	GameManager gm;
	ApplicationPanel panel;
	String requestWords;
	String requestTypes;

	public RequestSwapController(GameManager gm, ApplicationPanel panel, String requestWords, String requestTypes) {
		this.gm = gm;
		this.panel = panel;
		this.requestWords = requestWords;
		this.requestTypes = requestTypes;
	}

	public void process() {
		// let's make the request. We are requesting, and we want to have requested word for
		// the offer word.
		SwapManager sm = gm.getSwapManager();
		sm.setRequestWords(this.requestWords.split(","));
		sm.setRequestTypes(this.requestTypes.split(","));
		String req = sm.formatSwapRequest();
		
		String swapMsg = IProtocol.requestSwapMsg + IProtocol.separator + req;
		sm.sendMessage(swapMsg);
		panel.getSwapButton().setEnabled(false);
	}
}
