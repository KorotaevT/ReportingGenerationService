package cs.vsu.ReportingGenerationService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="database")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Database {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String url;

    private String username;

    private String password;

    @OneToMany(mappedBy = "database")
    private List<Tables> tables;
}
