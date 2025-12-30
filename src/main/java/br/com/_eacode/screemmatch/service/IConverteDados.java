package br.com._eacode.screemmatch.service;

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);   
}