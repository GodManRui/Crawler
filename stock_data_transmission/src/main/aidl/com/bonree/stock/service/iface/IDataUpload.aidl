package com.bonree.stock.service.iface;

import android.os.Bundle;

interface IDataUpload {
  /**
   * 把数据上传到{@link DataCollectionService}
   */
  void diliverData(String jsonData);
  
  void exceptionCaught(String message);
}