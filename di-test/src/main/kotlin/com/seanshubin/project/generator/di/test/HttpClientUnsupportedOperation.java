package com.seanshubin.project.generator.di.test;

import com.seanshubin.project.generator.di.contract.HttpClientContract;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

//
// This file was imported from: ../kotlin-reusable
// Module: di-test
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public class HttpClientUnsupportedOperation implements HttpClientContract {
    @Override
    public Optional<CookieHandler> cookieHandler() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Optional<Duration> connectTimeout() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public HttpClient.Redirect followRedirects() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Optional<ProxySelector> proxy() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public SSLContext sslContext() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public SSLParameters sslParameters() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Optional<Authenticator> authenticator() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public HttpClient.Version version() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public Optional<Executor> executor() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public WebSocket.Builder newWebSocketBuilder() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean awaitTermination(Duration duration) throws InterruptedException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public boolean isTerminated() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void shutdownNow() {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    @Override
    public void close() throws Exception {
        throw new UnsupportedOperationException("Not Implemented!");
    }
}
