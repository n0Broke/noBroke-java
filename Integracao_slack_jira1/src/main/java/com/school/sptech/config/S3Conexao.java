package com.school.sptech.config;

import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class S3Conexao {

    private static final String ACCESS_KEY = "";
    private static final String SECRET_KEY = "";
    private static final String SESSION_TOKEN = "";
    private static final String REGION = "us-east-1";


    public static S3Client conectar() {

        AwsSessionCredentials credenciais =
                AwsSessionCredentials.create(
                        ACCESS_KEY,
                        SECRET_KEY,
                        SESSION_TOKEN
                );

        return S3Client.builder()
                .region(Region.of(REGION))
                .credentialsProvider(
                        StaticCredentialsProvider.create(credenciais)
                )
                .build();
    }

    public static class S3Service {

        public static String buscarJson(String bucket, String key) {
            S3Client s3 = S3Conexao.conectar();

            GetObjectRequest request =
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build();

            ResponseBytes<GetObjectResponse> objeto =
                    s3.getObjectAsBytes(request);

            String json = objeto.asUtf8String();

            s3.close();
            return json;
        }
    }
}