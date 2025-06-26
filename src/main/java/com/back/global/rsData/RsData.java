package com.back.global.rsData;

public record RsData<T>(String resultCode, String msg, Object data) {
}