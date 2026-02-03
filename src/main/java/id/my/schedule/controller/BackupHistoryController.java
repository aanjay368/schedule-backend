package id.my.schedule.controller;

import id.my.schedule.entity.BackupHistory;
import id.my.schedule.entity.User;
import id.my.schedule.model.PagingResponse;
import id.my.schedule.model.WebResponse;
import id.my.schedule.model.backup_history.SearchBackupHistoryRequest;
import id.my.schedule.service.BackupHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/backups")
public class BackupHistoryController {

    @Autowired
    private BackupHistoryService backupHistoryService;

    @GetMapping(
            path = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<BackupHistory>> search(User user,
                                                   @RequestParam(name = "page", required = false) Integer page,
                                                   @RequestParam(name = "startDate",required = false) LocalDate startDate,
                                                   @RequestParam(name = "endDate",required = false) LocalDate endDate
                                                   ) {
        SearchBackupHistoryRequest request = SearchBackupHistoryRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                10,
                Sort.by("createdAt").descending()
        );
        Page<BackupHistory> response = backupHistoryService.search(user.getEmployee(), request, pageable);
        return WebResponse.<List<BackupHistory>>builder()
                .data(response.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(response.getNumber())
                        .totalPage(response.getTotalPages())
                        .totalElement(response.getTotalElements())
                        .build())
                .build();

    }

}
