package be.pxl.services.services;

import be.pxl.services.controller.dto.PostRequest;
import be.pxl.services.controller.dto.PostResponse;

import java.util.List;

public interface IPostService {
    void addPost(String role, String name, PostRequest postRequest);

    void updatePost(String role, String name, long id, PostRequest postRequest);

    List<PostResponse> findUnaccepted(String role);

    List<PostResponse> findAccepted();

    void approvePost(String role, String name, long id);
}
