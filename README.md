# 项目介绍

本项目是项目中用到的公共组件及部分封装方法

* JetPacket:
    * LiveData + ViewModel
    * 创建`Model`继承`ViewModel`，将数据包装到`MutableLiveData<T>`中,构造函数中初始化数据仓库`Repository` 
    
 ```
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
```


* Repository 层完成接口数据的获取以及转换为持久化数据。


 ```
        public class IndexRepository {
  private MutableLiveData<IndexInfo> indexInfoMutableLiveData;

  public IndexRepository(MutableLiveData<IndexInfo> indexInfoMutableLiveData) {
    this.indexInfoMutableLiveData = indexInfoMutableLiveData;
  }

  public void getIndexInfo() {
    Map<String, String> req = new HashMap<>();
    ApiService.createReqService(IndexApi.class)
        .indexInfo(req)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new SimpleSubscribe<BaseBean<IndexInfo>>() {
          @Override public void onNext(BaseBean<IndexInfo> indexInfoBaseBean) {
            super.onNext(indexInfoBaseBean);
            if (indexInfoBaseBean.success()) {
              indexInfoMutableLiveData.postValue(indexInfoBaseBean.getResult());
            }
          }
          @Override public void onError(Throwable throwable) {
            super.onError(throwable);
            indexInfoMutableLiveData.postValue(new IndexInfo("我是失败后的数据"));
          }
        });
  }
}
```
    
 
 * Activity层绑定数据
 

```
                  private void initModel() {
    indexModel = ViewModelProviders.of(this).get(IndexModel.class);
 Observer<IndexInfo> indexInfoObserver = new Observer<IndexInfo>() {
      @Override public void onChanged(IndexInfo indexInfo) {
        tvContent.setText(indexInfo.getContent());
      }
    };
    indexModel.getIndexInfoMutableLiveData().observe(this, indexInfoObserver);
  }  
  //请求更新数据
  indexModel.getIndexRepository().getIndexInfo())

```