package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.model.exam.Answer;
import yy.doctor.model.exam.Answer.TAnswer;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/4/28
 */
public class ExamEndActivity extends BaseActivity {

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_exam_end;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "考试结束", this);
    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViews() {
        List<Answer> items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Answer question = new Answer();
            question.put(TAnswer.answer, "答案" + i);
            question.put(TAnswer.id, "" + i);
            items.add(question);

        }
        //TODO:应该在点击最后一题的时候提交,这里只是显示结果
        exeNetworkReq(0, NetFactory
                .submitEx()
                .meetId("17042512131640894904")
                .moduleId("8")
                .paperId("2")
                .items(items)
                .builder());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result response = (Result) result;
        if (response.isSucceed()) {
            showToast("成功" + response.getCode());
        } else {
            showToast("失败" + response.getCode());
        }
    }
}
