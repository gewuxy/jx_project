package yy.doctor.model.form.edit;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import lib.yy.adapter.VH.FormVH;
import yy.doctor.model.form.FormType;

/**
 * @auther WangLan
 * @since 2017/7/4
 */

public class EdiRegisterNumForm extends EditNumberForm {
    private boolean isAdd;
    Activity mActivity;
    public EdiRegisterNumForm(Activity activity){
        mActivity = activity;
    }
    @NonNull
    @Override
    public int getType() {
        return FormType.et_register_num;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);
        holder.getEt().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after ==1) {
                    isAdd = true;
                }else {
                    isAdd = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if ((str.length() == 3 || str.length() == 8) && str.charAt(str.length() -1) != ' ' && before > count) {
                    str = str.substring(0,str.length()-1);
                    holder.getEt().setText(str);
                    holder.getEt().setSelection(str.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isAdd) {
                    if (null != holder.getEt()) {
                        String str =  s.toString();
                        if(!str.endsWith(" ")){
                            int length = s.length();
                            if (length == 3 || length == 8) {
                                String str1 = str + " "; //手动添加空格
                                holder.getEt().setText(str1);
                                holder.getEt().setSelection(str1.length());
                            }
                           if (length >= 13) {
                               hideBoard(mActivity);
                           }
                        }
                    }
                }
            }
        });
    }

        public static void hideBoard(Activity activity) {
            final View v = activity.getWindow().peekDecorView();
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }

    }
