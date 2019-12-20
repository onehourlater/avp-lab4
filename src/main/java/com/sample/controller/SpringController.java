package com.sample.controller;

import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.sample.JDBC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Map;

import static java.util.Map.entry;

@Controller
public class SpringController {

    private static final String SUCCESS_STATUS = "success";
    private static final String ERROR_STATUS = "error";

    JDBC jdbc = new JDBC();

    @Autowired
    private HazelcastInstance instance;

    @RequestMapping("/")
    public String main(Model model) {

        return "index"; //view
    }

    @RequestMapping(value = "/new_article", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public BaseResponse newArticle(@RequestBody Map<String, String> data) {

        String authorName = data.get("authorName");
        String body = data.get("body");

        jdbc.addArticle(authorName, body);

        jdbc.printArticles();

        ArrayList<Map<String, String>> articles = jdbc.getArticles();

        //

        IList<Map<String, String>> hazelArrayListUsers = instance.getList("subscribers");

        ArrayList<Map<String, String>> allSubscribers = jdbc.getSubscribers();

        if(allSubscribers.size() != hazelArrayListUsers.size()) {
            boolean subscriberInHazel = false;
            for (int i = 0; i < allSubscribers.size(); i++) {
                subscriberInHazel = false;
                for (int j = 0; j < hazelArrayListUsers.size(); j++) {
                    if (Integer.parseInt(allSubscribers.get(i).get("id")) == Integer.parseInt(hazelArrayListUsers.get(j).get("id"))) {
                        subscriberInHazel = true;
                        break;
                    }
                }
                if (!subscriberInHazel) hazelArrayListUsers.add(allSubscribers.get(i));
            }
        }

        //

        IList<Map<String, String>> hazelArrayListArticles = instance.getList("articles");

        Map<String, String> articleMap = Map.ofEntries(
                entry("authorName", authorName),
                entry("body", body)
        );

        hazelArrayListArticles.add (articleMap);

        //

        final BaseResponse response;

        String json = new Gson().toJson(articles);

        response = new BaseResponse(SUCCESS_STATUS, json);

        return response;

    }


    @RequestMapping(value = "/articles", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse articles() {

        ArrayList<Map<String, String>> articles = jdbc.getArticles();

        String json = new Gson().toJson(articles);

        final BaseResponse response;

        response = new BaseResponse(SUCCESS_STATUS, json);

        return response;

    }

    @RequestMapping(value = "/new_subscriber", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public BaseResponse newSubscriber(@RequestBody Map<String, String> data) {

        jdbc.addSubscriber(data.get("email"));

        ArrayList<Map<String, String>> subscribers = jdbc.getSubscribers();

        final BaseResponse response;

        String json = new Gson().toJson(subscribers);

        response = new BaseResponse(SUCCESS_STATUS, json);

        return response;

    }

    @RequestMapping(value = "/subscribers", method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse subscribers() {

        ArrayList<Map<String, String>> subscribers = jdbc.getSubscribers();

        String json = new Gson().toJson(subscribers);

        final BaseResponse response;

        response = new BaseResponse(SUCCESS_STATUS, json);

        return response;

    }
}

