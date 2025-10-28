package org.example.springproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.springproject.entity.Attraction;
import org.example.springproject.service.IAttractionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/attractions")
@Tag(name = "景点管理", description = "景点信息的增删改查接口")
public class AttractionController {

    @Autowired
    private IAttractionService attractionService;

    @Operation(summary = "获取所有景点", description = "获取景点列表,返回所有景点信息")
    @GetMapping("/list")
    public List<Attraction> getAllAttractions() {
        return attractionService.list();
    }

    @Operation(summary = "根据ID获取景点", description = "根据景点ID获取单个景点的详细信息")
    @GetMapping("/getAttractionById")
    public Attraction getAttractionById(
            @Parameter(description = "景点ID", required = true) @RequestParam Integer id) {
        return attractionService.getById(id);
    }

    @Operation(summary = "添加景点", description = "添加新的景点信息")
    @PostMapping("/addAttraction")
    public boolean addAttraction(
            @Parameter(description = "景点对象", required = true) @RequestBody Attraction attraction) {
        return attractionService.save(attraction);
    }

    @Operation(summary = "更新景点", description = "更新景点信息,需要提供完整的景点对象(包含ID)")
    @PutMapping("/updateAttraction")
    public boolean updateAttraction(
            @Parameter(description = "景点对象", required = true) @RequestBody Attraction attraction) {
        return attractionService.updateById(attraction);
    }

    @Operation(summary = "删除景点", description = "根据景点ID删除指定的景点")
    @DeleteMapping("/deleteAttraction")
    public boolean deleteAttraction(
            @Parameter(description = "景点ID", required = true) @RequestParam Integer id) {
        return attractionService.removeById(id);
    }
}
