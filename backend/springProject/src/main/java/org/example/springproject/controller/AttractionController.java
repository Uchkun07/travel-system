package org.example.springproject.controller;

import org.example.springproject.entity.Attraction;
import org.example.springproject.service.IAttractionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/attractions")
public class AttractionController {

    @Autowired
    private IAttractionService attractionService;

    @GetMapping("/list")
    public List<Attraction> getAllAttractions() {
        return attractionService.list();
    }

    @GetMapping("/getAttractionById")
    public Attraction getAttractionById(@RequestParam Integer id) {
        return attractionService.getById(id);
    }

    @PostMapping("/addAttraction")
    public boolean addAttraction(@RequestBody Attraction attraction) {
        return attractionService.save(attraction);
    }

    @PutMapping("/updateAttraction")
    public boolean updateAttraction(@RequestBody Attraction attraction) {
        return attractionService.updateById(attraction);
    }

    @DeleteMapping("/deleteAttraction")
    public boolean deleteAttraction(@RequestParam Integer id) {
        return attractionService.removeById(id);
    }
}
