package id.my.schedule.model;

import id.my.schedule.entity.EmployeeDivision;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadScheduleRequest {

    private String filename;

    private InputStream inputStream;

    private EmployeeDivision division;

}
