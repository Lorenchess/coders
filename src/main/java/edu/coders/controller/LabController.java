package edu.coders.controller;

import edu.coders.services.LabService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/labs")
@RequiredArgsConstructor
public class LabController {

    private final LabService labService;
}
