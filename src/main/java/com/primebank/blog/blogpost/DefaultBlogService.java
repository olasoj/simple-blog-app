package com.primebank.blog.blogpost;

import com.primebank.blog.author.model.Author;
import com.primebank.blog.blogpost.model.*;
import com.primebank.blog.blogpost.model.request.CreateBlogPostRequest;
import com.primebank.blog.blogpost.model.request.EditBlogPostRequest;
import com.primebank.blog.blogpost.repository.BlogRepository;
import com.primebank.blog.blogpost.repository.DefaultBlogRepository;
import com.primebank.blog.user.model.BlogUserPrincipal;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

@Component
public class DefaultBlogService implements BlogService {
    private final BlogRepository blogRepository;

    public DefaultBlogService() {
        this.blogRepository = DefaultBlogRepository.blogRepository;
    }

    @Override
    public CreateBlogPostResult createBlogPost(CreateBlogPostRequest createBlogPostRequest, BlogUserPrincipal blogUserPrincipal) {
        Assert.notNull(createBlogPostRequest, "CreateBlogPostRequest cannot be null");
        Assert.notNull(blogUserPrincipal, "BlogUserPrincipal cannot be null");

        Blog blog = Blog.builder()
                .title(createBlogPostRequest.title())
                .body(createBlogPostRequest.body())
                .author(new Author(blogUserPrincipal.user()))
                .build();

        blogRepository.saveBlogPost(blog);
        return new CreateBlogPostResult(blog);
    }

    @Override
    public EditBlogPostResult editBlogPost(String blogId, EditBlogPostRequest editBlogPostRequest, BlogUserPrincipal blogUserPrincipal) {
        Assert.notNull(blogId, "BlogId cannot be null");
        Assert.notNull(editBlogPostRequest, "EditBlogPostRequest cannot be null");
        Assert.notNull(blogUserPrincipal, "BlogUserPrincipal cannot be null");

        ResponseStatusException responseStatusException = new ResponseStatusException(HttpStatusCode.valueOf(404), "Blog post not found");

        Blog blog = blogRepository.findBlogTitle(blogId)
                .orElseThrow(() -> responseStatusException);

        blog.setTitle(editBlogPostRequest.title());
        blog.setBody(editBlogPostRequest.body());

        blogRepository.updateBlogTitle(blog);
        return new EditBlogPostResult(blog);
    }

    @Override
    public DeleteBlogPostResult deleteBlogPost(String blogId) {
        blogRepository.deleteBlogTitle(blogId);

        return new DeleteBlogPostResult();
    }

    @Override
    public ReadBlogPostResult readBlogPost() {
        return new ReadBlogPostResult(blogRepository.findAllBlogPost());
    }
}
