package com.weexbox.core.net.callback;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by freeson on 16/7/28.<br/>
 * <p/>
 * 使用方法:<br/>
 * <p/>
 * OkHttpUtils.post().url(url)
 * .build()
 * .execute(new HttpEntityCallback<TestEntity>() {
 *
 * @Override protected void onSuccess(TestEntity entity, int id) {
 * System.out.println("---response.imageUrl--->" + entity.imageUrl);
 * }
 * @Override protected void onFail(Call call, int errorCode, int id) {
 * <p/>
 * }
 * });
 */
public abstract class HttpEntityCallback<T> extends HttpCallback<T> {

    @Override
    protected T parseEntity(String data, int requestId) throws Exception {
        T object = null;
        Type t = getClass().getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
//            if (isList()) {
//                Class<T> entityClass = (Class<T>) p[0];
//                object = new Gson().fromJson(data, entityClass);
//            } else {
//                Class<T> entityClass = (Class<T>) p[0];
//                object = new Gson().fromJson(data, entityClass);
//            }

            
            if (isList()) {
                object = JSON.parseObject(data, p[0]);
            } else {
                Class<T> entityClass = (Class<T>) p[0];
                object = JSON.parseObject(data, entityClass);
            }
        }

        return object;
    }

    /**
     * 如果最外层数据直接就是一个数组的话, 请返回true, 或者使用{@link HttpListEntityCallback}
     *
     * @return
     */
    protected boolean isList() {
        return false;
    }
}
