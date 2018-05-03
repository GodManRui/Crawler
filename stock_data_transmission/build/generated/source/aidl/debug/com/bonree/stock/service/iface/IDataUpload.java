/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\QQFile\\854463112\\FileRecv\\jitsi-meet-master\\Crawler\\stock_data_transmission\\src\\main\\aidl\\com\\bonree\\stock\\service\\iface\\IDataUpload.aidl
 */
package com.bonree.stock.service.iface;
public interface IDataUpload extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.bonree.stock.service.iface.IDataUpload
{
private static final java.lang.String DESCRIPTOR = "com.bonree.stock.service.iface.IDataUpload";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.bonree.stock.service.iface.IDataUpload interface,
 * generating a proxy if needed.
 */
public static com.bonree.stock.service.iface.IDataUpload asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.bonree.stock.service.iface.IDataUpload))) {
return ((com.bonree.stock.service.iface.IDataUpload)iin);
}
return new com.bonree.stock.service.iface.IDataUpload.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_diliverData:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.diliverData(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_exceptionCaught:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.exceptionCaught(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.bonree.stock.service.iface.IDataUpload
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
   * 把数据上传到{@link DataCollectionService}
   */
@Override public void diliverData(java.lang.String jsonData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(jsonData);
mRemote.transact(Stub.TRANSACTION_diliverData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void exceptionCaught(java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_exceptionCaught, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_diliverData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_exceptionCaught = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
/**
   * 把数据上传到{@link DataCollectionService}
   */
public void diliverData(java.lang.String jsonData) throws android.os.RemoteException;
public void exceptionCaught(java.lang.String message) throws android.os.RemoteException;
}
