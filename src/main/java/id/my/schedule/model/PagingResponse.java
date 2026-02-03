package id.my.schedule.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagingResponse {

    private Integer currentPage;

    private Integer totalPage;

    private Long totalElement;

}
