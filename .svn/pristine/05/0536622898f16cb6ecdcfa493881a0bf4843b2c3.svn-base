package com.xiaoxun.xun.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created on 2017/11/21 下午1:45.
 * leo linxiaotao1993@vip.qq.com
 */
@SuppressWarnings("unused")
public final class CollectionsUtils {

    private CollectionsUtils() {
        throw new UnsupportedOperationException("tools class can not call constructors");
    }


    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }

    public static <T> List<T> getSnapshot(Collection<T> other) {
        List<T> result = new ArrayList<>(other.size());
        result.addAll(other);
        return result;
    }

    public static int getSize(Collection collection) {
        return isEmpty(collection) ? 0 : collection.size();
    }

    /***
     * 方法一对集合进行深拷贝 注意需要对泛型类进行序列化(实现Serializable)
     *
     * @param srcList
     * @param <T>
     * @return
     */
    public static <T> List<T> depCopy(List<T> srcList) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(srcList);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream inStream = new ObjectInputStream(byteIn);
            List<T> destList = (List<T>) inStream.readObject();
            return destList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
