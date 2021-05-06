package com.liuying.jetpacket.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.liuying.jetpacket.model.IndexInfo;
import com.liuying.jetpacket.repository.IndexRepository;

/**
 * 首页数据Model
 * Created by liuying on 5/6/21 16:19.
 * Email: ly1203575492@163.com
 */
public class IndexModel extends ViewModel {
  private MutableLiveData<IndexInfo> mIndexInfoMutableLiveData = new MutableLiveData<>();

  //数据仓库
  private IndexRepository mIndexRepository;

  public IndexModel() {
    mIndexRepository = new IndexRepository(mIndexInfoMutableLiveData);
  }

  public MutableLiveData<IndexInfo> getIndexInfoMutableLiveData() {
    if (mIndexInfoMutableLiveData == null) mIndexInfoMutableLiveData = new MutableLiveData<>();
    return mIndexInfoMutableLiveData;
  }

  public IndexRepository getIndexRepository() {
    return mIndexRepository;
  }

  public void getIndexInfo() {
    mIndexRepository.getIndexInfo();
  }
}
