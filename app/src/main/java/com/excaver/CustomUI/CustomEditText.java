package com.excaver.CustomUI;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    Context context;
    private static Activity ChatRoom;;

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if(ChatRoom != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            KeyEvent.DispatcherState state = getKeyDispatcherState();

            if(state != null){
                if(event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0){

                    InputMethodManager mgr = (InputMethodManager)
                            context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
                }
                else if(event.getAction() == KeyEvent.ACTION_UP && !event.isCanceled() && state.isTracking(event)){
                    ChatRoom.onBackPressed();
                    return true;
                }
            }
        }


        return super.dispatchKeyEventPreIme(event);
    }

}