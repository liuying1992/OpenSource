package com.liuying.jetpacket;

import android.os.Bundle;
import android.widget.TextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.liuying.jetpacket.base.BaseActivity;
import com.liuying.jetpacket.data.IndexModel;
import com.liuying.jetpacket.model.IndexInfo;

public class MainActivity extends BaseActivity {
  private TextView tvContent;
  private IndexModel indexModel;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tvContent = findViewById(R.id.tv_content);
    initModel();
    findViewById(R.id.btn_load).setOnClickListener(
        v -> indexModel.getIndexRepository().getIndexInfo());
  }

  private void initModel() {
    indexModel = ViewModelProviders.of(this).get(IndexModel.class);

    Observer<IndexInfo> indexInfoObserver = new Observer<IndexInfo>() {
      @Override public void onChanged(IndexInfo indexInfo) {
        tvContent.setText(indexInfo.getContent());
      }
    };
    indexModel.getIndexInfoMutableLiveData().observe(this, indexInfoObserver);
  }
}