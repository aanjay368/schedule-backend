package id.my.schedule.service.implementation;

import id.my.schedule.entity.Division;
import id.my.schedule.model.division.DivisionResponse;
import id.my.schedule.repository.DivisionRepository;
import id.my.schedule.service.DivisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DivisionServiceImpl implements DivisionService {

    @Autowired
    private DivisionRepository divisionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DivisionResponse> getList() {
        List<Division> divisions = divisionRepository.findAll();
        return divisions.stream().map(DivisionResponse::toDivisionResponse).toList();
    }

}
