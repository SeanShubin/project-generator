package com.seanshubin.project.generator.di.contract;

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
// Module: di-contract
//
// Before editing this file, consider whether updating the source project
// and re-importing would be a better approach.
//

public interface HttpClientContract extends AutoCloseable {
    Optional<CookieHandler> cookieHandler();

    Optional<Duration> connectTimeout();

    HttpClient.Redirect followRedirects();

    Optional<ProxySelector> proxy();

    SSLContext sslContext();

    SSLParameters sslParameters();

    Optional<Authenticator> authenticator();

    HttpClient.Version version();

    Optional<Executor> executor();

    <T> HttpResponse<T>
    send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
            throws IOException, InterruptedException;

    <T> CompletableFuture<HttpResponse<T>>
    sendAsync(HttpRequest request,
              HttpResponse.BodyHandler<T> responseBodyHandler);

    <T> CompletableFuture<HttpResponse<T>>
    sendAsync(HttpRequest request,
              HttpResponse.BodyHandler<T> responseBodyHandler,
              HttpResponse.PushPromiseHandler<T> pushPromiseHandler);

    WebSocket.Builder newWebSocketBuilder();

    void shutdown();

    boolean awaitTermination(Duration duration) throws InterruptedException;

    boolean isTerminated();

    void shutdownNow();
}
