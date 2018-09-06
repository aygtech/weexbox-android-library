package com.weexbox.core.net.callback;

/**
 * Created by freeson on 16/7/28.
 * <p/>
 * OkHttpUtils.post().url(url)
 * .build()
 * .execute(new HttpListEntityCallback<List<TestListEntity>>() {
 *
 * @Override protected void onSuccess(List<TestListEntity> entity, int id) {
 * System.out.println("------>" + entity.size());
 * }
 * @Override protected void onFail(Call call, int code, int id) {
 * <p/>
 * }
 * });
 */


public abstract class HttpListEntityCallback<T> extends HttpEntityCallback<T> {

    @Override
    protected boolean isList() {
        return true;
    }
}
