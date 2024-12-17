package edu.coders.services;

import edu.coders.dtos.LabDTO;
import edu.coders.repositories.LabRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LabServiceImpl implements LabService {

    private final LabRepository labRepository;


    @Override
    public LabDTO getLabById(Long id) {

        return null;
    }
}
