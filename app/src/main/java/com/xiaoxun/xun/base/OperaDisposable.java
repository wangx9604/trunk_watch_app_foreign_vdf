package com.xiaoxun.xun.base;



import androidx.annotation.NonNull;

import io.reactivex.disposables.Disposable;



public interface OperaDisposable {

    void addDisposable(@NonNull Disposable disposable);
}
