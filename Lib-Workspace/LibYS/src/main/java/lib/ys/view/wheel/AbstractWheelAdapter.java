/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package lib.ys.view.wheel;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lib.ys.AppEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.adapter.interfaces.IViewHolder;
import lib.ys.fitter.LayoutFitter;
import lib.ys.util.GenericUtil;
import lib.ys.util.ReflectionUtil;

/**
 * Abstract Wheel adapter.
 */
abstract public class AbstractWheelAdapter<T, VH extends ViewHolderEx> implements WheelViewAdapter {

    private LayoutInflater mInflater = null;

    private List<T> mTs;

    private Class<VH> mVHClass;

    public AbstractWheelAdapter() {
        mVHClass = GenericUtil.getClassType(getClass(), IViewHolder.class);
    }

    @Override
    public View getEmptyItem(View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }

    public void setData(List<T> data) {
        mTs = data;
    }

    @Override
    public int getItemsCount() {
        return mTs == null ? 0 : mTs.size();
    }

    @Override
    public View getItem(int position, View convertView, ViewGroup parent) {
        /**
         * 如果没有生成convertView
         */
        if (convertView == null) {
            convertView = getLayoutInflater().inflate(getConvertViewResId(), null);
            LayoutFitter.fit(convertView);

            VH holder = ReflectionUtil.newInst(mVHClass, convertView);
            convertView.setTag(holder);

            initView(position, holder);
        }

        refreshView(position, (VH) convertView.getTag());

        return convertView;
    }

    public LayoutInflater getLayoutInflater() {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(getContext());
        }
        return mInflater;
    }

    public Context getContext() {
        return AppEx.ct();
    }

    /**
     * 用于设置一次性属性, 比如图片的圆角处理, view的大小位置等
     *
     * @param position
     * @param holder
     */
    protected void initView(int position, VH holder) {
    }

    public T getData(int position) {
        if (mTs == null) {
            return null;
        }

        T t = null;
        try {
            t = mTs.get(position);
        } catch (Exception e) {

        }

        return t;
    }

    /**
     * 是否使用自动适配
     *
     * @return
     */
    protected boolean useAutoFit() {
        return true;
    }

    abstract protected void refreshView(int position, VH holder);

    abstract public int getConvertViewResId();
}
