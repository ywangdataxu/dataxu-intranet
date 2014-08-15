package dataxu.intranet.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import dataxu.intranet.entity.Chapter;
import dataxu.intranet.repository.ChapterRepository;

@Controller
public class ChapterController {
    @Autowired
    private ChapterRepository chapterRepository;

    @RequestMapping(value = "/api/chapters", method = RequestMethod.GET)
    @ResponseBody
    public List<Chapter> getChapters() {
        List<Chapter> result = chapterRepository.findAll();
        Collections.sort(result);

        return result;
    }
}