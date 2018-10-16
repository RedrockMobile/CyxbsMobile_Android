package com.mredrock.cyxbs.common.network.converter;


import com.mredrock.cyxbs.common.network.converter.annotation.JsonApi;
import com.mredrock.cyxbs.common.network.converter.annotation.XmlApi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by cc on 16/5/8.
 */
public class QualifiedTypeConverterFactory extends Converter.Factory {
    private final Converter.Factory jsonFactory;
    private final Converter.Factory xmlFactory;

    public QualifiedTypeConverterFactory(Converter.Factory jsonFactory, Converter.Factory xmlFactory) {
        this.jsonFactory = jsonFactory;
        this.xmlFactory = xmlFactory;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof JsonApi) {
                return jsonFactory.responseBodyConverter(type, annotations, retrofit);
            }
            if (annotation instanceof XmlApi) {
                return xmlFactory.responseBodyConverter(type, annotations, retrofit);
            }
        }
        return jsonFactory.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        for (Annotation annotation : parameterAnnotations) {
            if (annotation instanceof JsonApi) {
                return jsonFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations,
                        retrofit);
            }
            if (annotation instanceof XmlApi) {
                return xmlFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations,
                        retrofit);
            }
        }
        return jsonFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations,
                retrofit);
    }
}
