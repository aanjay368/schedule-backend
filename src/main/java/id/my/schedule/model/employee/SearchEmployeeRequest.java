package id.my.schedule.model.employee;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SearchEmployeeRequest {

    private String name;

    private Integer posisitionId;

    private Integer divisionId;

}
