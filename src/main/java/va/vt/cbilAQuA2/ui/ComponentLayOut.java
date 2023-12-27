package va.vt.cbilAQuA2.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class ComponentLayOut {
	
	public static GridBagLayout iniGridBagLayout () {
		GridBagLayout gbl = new GridBagLayout();
		return gbl;
	}
	
	public static GridBagConstraints iniGridBagConstraints () {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		return gbc;
	}
	
	public static void add(Container jfr,GridBagLayout gbl,Component comp,GridBagConstraints gbc,int gridx,int gridy,int gridwidth,int gridheight,int weight_x,int weight_y)
	{
		gbc.weightx=weight_x;
		gbc.weighty=weight_y;
		gbc.gridheight=gridheight;
		gbc.gridwidth=gridwidth;
		gbc.gridx=gridx;
		gbc.gridy=gridy;
		
		gbl.setConstraints(comp, gbc);
		jfr.add(comp);
	}
}
