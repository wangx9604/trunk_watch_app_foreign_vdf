/**
 * Creation Date:2015-6-30
 * 
 * Copyright 
 */
package com.xiaoxun.xun.activitys;

import com.xiaoxun.xun.beans.DialogSet;
import com.xiaoxun.xun.utils.DialogUtil;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;

/**
 * Description Of The Class<br>
 *
 * @author 	liutianxiang
 * @version 1.000, 2015-6-30
 * 
 */
public class BlankActivity extends NormalActivity{
    
    DialogSet set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        int key = getIntent().getIntExtra("key", 0);
        
        set = myApp.getSysDialogSets().get(key);
        if (set!=null){
            Dialog dlg;
            if (set.leftListener== null){
                if (set.description != null && set.description.length() > 0) {
                    dlg = DialogUtil.CustomNormalSpannedDialog(BlankActivity.this,
                            set.titile,
                            set.description,
                            set.rightListener,
                            set.rightBtnStr);
                } else {
                    dlg = DialogUtil.CustomNormalDialog(BlankActivity.this,
                            set.titile,
                            set.desc,
                            set.rightListener,
                            set.rightBtnStr);
                }
            }else{
             dlg = DialogUtil.CustomNormalDialog(BlankActivity.this,
                    set.titile, 
                    set.desc,
                    set.leftListener,  
                    set.leftBtnStr,
                    set.rightListener,  
                    set.rightBtnStr);
            
            }      
            dlg.setOnDismissListener(new OnDismissListener() {
                
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });

            dlg.setCancelable(false);
            dlg.setCanceledOnTouchOutside(false);
            dlg.show();
            myApp.getSysDialogSets().remove(key);
        }else{
            finish();
        }

    
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
    
}
