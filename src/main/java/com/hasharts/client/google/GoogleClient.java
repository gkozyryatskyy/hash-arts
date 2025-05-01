package com.hasharts.client.google;

import com.hasharts.client.google.model.RequestDto;
import com.hasharts.client.google.model.ResponseDto;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.inject.Default;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Default
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "google-api")
public interface GoogleClient {

//    curl -s -X POST \
//            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp-image-generation:generateContent?key=$GEMINI_API_KEY" \
//            -H "Content-Type: application/json" \
//            -d '{
//            "contents": [{
//        "parts": [
//        {"text": "Hi, can you create a 3d rendered image of a pig with wings and a top hat flying over a happy futuristic scifi city with lots of greenery?"}
//      ]
//    }],
//            "generationConfig":{"responseModalities":["TEXT","IMAGE"]}
//}' \
//  | grep -o '"data": "[^"]*"' \
//  | cut -d'"' -f4 \
//  | base64 --decode > gemini-native-image.png

    @POST
    Uni<ResponseDto> generate(@QueryParam("key") String key, RequestDto request);
}
