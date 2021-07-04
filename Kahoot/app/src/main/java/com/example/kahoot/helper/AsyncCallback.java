package com.example.kahoot.helper;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AsyncCallback {
    public interface RunOffUIThread<T> {
        public Response<T> runOffUIThread() throws Exception;
    }
    public interface RunOffUIThreadError<T> {
        public Response<T> runOffUIThread(Exception e);
    }
    public interface RunOnUIThread<T> {
        public void runOnUIThread(Response<T> response);
    }
    public abstract static class Response<T>
    {
        private Response() {}

        public static final class Success<T> extends Response<T>
        {
            public T data;

            public Success(T data)
            {
                this.data = data;
            }
        }

        public static final class Error<T> extends Response<T>
        {
            public Exception exception;

            public Error(Exception exception)
            {
                this.exception = exception;
            }
        }
    }
    public static <T> Future<?> start(ExecutorService executorService, RunOffUIThread<T> runOffUIThread, RunOffUIThreadError<T> runOffUIThreadError, RunOnUIThread<T> runOnUIThread) {
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        return executorService.submit(() -> {
            try {
                Response<T> response = runOffUIThread.runOffUIThread();
                handler.post(() -> {
                    runOnUIThread.runOnUIThread(response);
                });
            } catch (Exception e) {
                Response<T> response = runOffUIThreadError.runOffUIThread(e);
                handler.post(() -> {
                    runOnUIThread.runOnUIThread(response);
                });
            }
        });
    }
}
