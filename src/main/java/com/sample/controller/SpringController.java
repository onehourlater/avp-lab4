package com.sample.controller;

import com.google.gson.Gson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.sample.JDBC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.locks.Lock;

import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Map;

import static java.util.Map.entry;

@Controller
public class SpringController {

    private static final String SUCCESS_STATUS = "success";
    private static final String ERROR_STATUS = "error";

    private HazelcastInstance instance;

    JDBC jdbc = new JDBC();

    @Autowired
    SpringController(HazelcastInstance instance) {
        this.instance = instance;

        // Блокируем список с подписчиками (hazelArrayListUsers) от внешних изменений
        Lock lock = instance.getLock("hazelArrayListUsers");
        lock.lock();
        try
        {
            // Берем список подписчиков из Хазелкаста
            IList<Map<String, String>> hazelArrayListUsers = instance.getList("subscribers");

            // Берем список подписчиков из БД
            ArrayList<Map<String, String>> allSubscribers = jdbc.getSubscribers();

            // Добавляем их в Хазелкаст, если их там еще нет (по их Email)
            boolean subscriberInHazel = false;
            for (Map<String, String> allSubscriber : allSubscribers) {
                subscriberInHazel = false;
                for (int j = 0; j < hazelArrayListUsers.size(); j++) {
                    if (allSubscriber.get("email").equals(hazelArrayListUsers.get(j).get("email"))) {
                        subscriberInHazel = true;
                        break;
                    }
                }
                if (!subscriberInHazel) hazelArrayListUsers.add(allSubscriber);
            }
        }
        finally
        {
            lock.unlock();
        }

    }

    @RequestMapping("/")
    public String main(Model model) {

        return "index"; //view
    }

    @RequestMapping(value = "/new_article", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public BaseResponse newArticle(@RequestBody Map<String, String> data) {

        String authorName = data.get("authorName");
        String body = data.get("body");

        // Добавляем статью в БД
        jdbc.addArticle(authorName, body);

        // Создаем мап
        Map<String, String> articleMap = Map.ofEntries(
                entry("authorName", authorName),
                entry("body", body)
        );

        // Берем статьи из Хазелкаста
        IList<Map<String, String>> hazelArrayListArticles = instance.getList("articles");

        // Добавляем статью в Хазелкаст
        hazelArrayListArticles.add (articleMap);

        // Формируем JSON из статей из БД и отправляем на фронт

        final BaseResponse response;

        ArrayList<Map<String, String>> articles = jdbc.getArticles();

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

        // Создаем мап
        Map<String, String> subscriberMap = Map.ofEntries(
                entry("email", data.get("email"))
        );

        // Берем подписчиков из Хазелкаста
        IList<Map<String, String>> hazelArrayListSubscribers = instance.getList("subscribers");

        // Добавляем подписчика в Хазелкаст
        hazelArrayListSubscribers.add (subscriberMap);

        // Формируем JSON из подписчиков и отправлем на фронт

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

