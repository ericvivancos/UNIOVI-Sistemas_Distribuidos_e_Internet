package com.example.sdi2324entrega192.services;

import com.example.sdi2324entrega192.entities.Post;
import com.example.sdi2324entrega192.entities.Recommendation;
import com.example.sdi2324entrega192.entities.User;
import com.example.sdi2324entrega192.repositories.RecommendationRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;


@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;

    public RecommendationService(RecommendationRepository recommendationRepository){ this.recommendationRepository = recommendationRepository;}

    public boolean alreadyRecommended(User user, Post post) {
        return recommendationRepository.alreadyRecommended(user, post);
    }

    public void addRecomendation(User user, Post post) {
        Recommendation recommendation = new Recommendation(user, post);
        user.getRecommendedPosts().add(recommendation);
        post.getUsersRecommend().add(recommendation);
        recommendationRepository.save(recommendation);
    }

    public Page<Post> getRecommendedPosts(Pageable pageable, User user){
        return recommendationRepository.findByUser(pageable, user);
    }


}
