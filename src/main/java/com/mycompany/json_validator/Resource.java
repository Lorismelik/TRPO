/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.json_validator;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.google.gson.*;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

@Path("")

public class Resource {

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    /**
     * Vadidates JSON file.
     *
     * @throws IOException
     * @param InputStream stream
     * @param FormDataContentDisposition fileDetail
     *
     * @return Response
     */
    public Response checkFile(@FormDataParam("file") InputStream stream, @FormDataParam("file") FormDataContentDisposition fileDetail) {
        final String json = new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Object result;
        try {
            result = gson.fromJson(json, Object.class);
        } catch (JsonSyntaxException e) {
            return Response.status(200).entity(gson.toJson(makeError(e, fileDetail))).build();
        }
        return Response.status(200).entity(gson.toJson(result)).build();
    }

    /**
     * Makes error message.
     *
     * @throws IOException
     * @param JsonSyntaxException e
     * @param FormDataContentDisposition fileDetail
     *
     * @return Map<String, String>
     */
    private Map<String, String> makeError(JsonSyntaxException e, FormDataContentDisposition fileDetail) {
        String messageDetail = e.getCause().getMessage();
        Map<String, String> error = new HashMap<>();
        String[] arrayMessage = messageDetail.split(" at", 2);
        error.put("resource", fileDetail.getFileName());
        error.put("request-id", "12345");
        error.put("errorCode", "12345");
        error.put("errorPlace", arrayMessage[1]);
        error.put("errorMessage", arrayMessage[0]);
        return error;
    }
}
