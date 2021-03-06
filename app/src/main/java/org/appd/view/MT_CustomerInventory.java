/*************************************************************************************
 * Product: SFAndroid (Sales Force Mobile)                                           *
 * This program is free software; you can redistribute it and/or modify it           *
 * under the terms version 2 of the GNU General Public License as published          *
 * by the Free Software Foundation. This program is distributed in the hope          *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied        *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                  *
 * See the GNU General Public License for more details.                              *
 * You should have received a copy of the GNU General Public License along           *
 * with this program; if not, write to the Free Software Foundation, Inc.,           *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                            *
 * For the text or an alternative of this public license, you may reach us           *
 * Copyright (C) 2012-2012 E.R.P. Consultores y Asociados, S.A. All Rights Reserved. *
 * Contributor(s): Yamel Senih www.erpconsultoresyasociados.com                      *
 *************************************************************************************/
package org.appd.view;

import org.appd.base.Env;
import org.appd.base.R;
import org.appd.model.MBVisit;
import org.appd.util.Msg;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * @author Yamel Senih
 *
 */
public class MT_CustomerInventory extends MTabActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        
    	addActivity(MV_CustomerInventory.class, "XX_MB_CustomerInventory", 
    			R.string.XX_MB_CustomerInventory_ID,R.drawable.package_m);
    	addActivity(MV_CustomerInventoryLine.class, "XX_MB_CustomerInventoryLine", 
    			R.string.XX_MB_CustomerInventoryLine_ID, R.drawable.inventory_m);
	}
	
	@Override
	protected void e_ButtNew(){
		super.e_ButtNew();
		int tabIndex = mTabHost.getCurrentTab();
		if(tabIndex == 0){
			if(record_ID == 0){
				mTabHost.getTabWidget().getChildAt(1).setEnabled(false);
			}
		}
	}
	
	@Override
	protected void e_ButtModify(){
		super.e_ButtModify();
		int tabIndex = mTabHost.getCurrentTab();
		if(tabIndex == 0)
			mTabHost.getTabWidget().getChildAt(1).setEnabled(false);
	}
	
	@Override
	protected void e_ButtBack(){
		super.e_ButtBack();
		int tabIndex = mTabHost.getCurrentTab();
		if(tabIndex == 0){
			if(record_ID != 0){
				mTabHost.getTabWidget().getChildAt(1).setEnabled(true);
			}
		}
	}
	
	@Override
	protected void voidConfig(){
		super.voidConfig();
		int tabIndex = mTabHost.getCurrentTab();
		if(tabIndex == 0){
			mTabHost.getTabWidget().getChildAt(1).setEnabled(false);
		}
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        int tabIndex = mTabHost.getCurrentTab();
		if(tabIndex == 0){
			if(Env.getTabRecord_ID(this, "XX_MB_CustomerInventory") > 0){
				mTabHost.getTabWidget().getChildAt(1).setEnabled(true);
			} else {
				mTabHost.getTabWidget().getChildAt(1).setEnabled(false);
			}
		} else if(tabIndex == 1){
			voidConfig();
			buttNew.setEnabled(false);
		}
    }
	
	@Override
	public void onTabChanged(String tabId) {
		super.onTabChanged(tabId);
		int tabIndex = mTabHost.getCurrentTab();
		
		if(tabIndex == 1){
			setVisibleButtons(View.GONE);
		} else {
			setVisibleButtons(View.VISIBLE);
			MVActivity bp = (MVActivity)getCurrentActivity();
			bp.refresh();
		}
	}
	
	@Override
	protected void e_ButtSave(){
		MVActivity bp = (MVActivity)getCurrentActivity();
		if(bp != null){
			if(bp.isNew()){
				//	Verifica si la visita actual es la que esta abierta
				if(Env.getContextAsInt(this, "#XX_MB_Visit_ID") == 0){
					final int m_XX_MB_Visit_ID = MBVisit.findOpenVisit(this, null);
					//	Verifica si ya hay una visita abierta
					if(m_XX_MB_Visit_ID != 0){
						final MBVisit visit = new MBVisit(getApplicationContext(), m_XX_MB_Visit_ID);
						
						if(!Env.isAutomaticVisitClosing(this)){
							//	Get Date from Visit
							String dateS = (String)visit.get_Value("DateVisit");
							
							Builder ask = Msg.confirmMsg(this, Env.getDateFormatString(dateS, "yyyy-MM-dd hh:mm:ss", "dd/MM/yyyy hh:mm:ss") 
									+ "\n" + visit.get_Value("NameBPartner") + "\"" 
									+ "\n" + getResources().getString(R.string.msg_AskCloseVisit));
					    	
							String msg_Acept = this.getResources().getString(R.string.msg_Yes);
					    	ask.setPositiveButton(msg_Acept, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									
									dialog.cancel();
									/**
									 * Cierra la Visita anterior
									 */
									try {
										//MBVisit v = new MBVisit(getApplicationContext(), m_XX_MB_Visit_ID);
										
										visit.closeVisit(null);
										
										saveInventory();
									} catch(Exception e) {
										Msg.alertMsg(getCurrentActivity(), 
												getCurrentActivity().getString(R.string.msg_Error), e.getMessage());
										Log.e("Error", "Exception", e);
									}
								}
							});
					    	ask.show();
						} else {
							/**
							 * Cierra la Visita anterior
							 */
							try {
								//MBVisit v = new MBVisit(getApplicationContext(), m_XX_MB_Visit_ID);
								
								visit.closeVisit(null);
								
								saveInventory();
							} catch(Exception e) {
								Msg.alertMsg(getCurrentActivity(), 
										getCurrentActivity().getString(R.string.msg_Error), e.getMessage());
								Log.e("Error", "Exception", e);
							}
						}
					} else
						saveInventory();    	
				} else
					saveInventory();
			} else
				saveInventory();
		}
	}
	
	/**
	 * Guarda el inventario
	 * @author Yamel Senih 25/07/2012, 15:43:37
	 * @return void
	 */
	private void saveInventory(){
		super.e_ButtSave();
		int tabIndex = mTabHost.getCurrentTab();
		if(tabIndex == 0){
			if(record_ID != 0){
				mTabHost.getTabWidget().getChildAt(1).setEnabled(true);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.appd.view.MTabActivity#getActivityName()
	 */
	@Override
	protected String getActivityName() {
		return getClass().getSimpleName();
	}
	
}
