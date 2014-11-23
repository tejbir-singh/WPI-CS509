package cps.controller;

import java.awt.event.MouseAdapter;

import cps.model.GameManager;
import cps.view.ApplicationPanel;

public class RequestSwapController extends MouseAdapter {
	GameManager gm;
	ApplicationPanel panel;
	
	public RequestSwapController(GameManager gm, ApplicationPanel panel) {
		this.gm = gm;
		this.panel = panel;
	}

	public void process() {
		// TODO Auto-generated method stub
		
	}

}
