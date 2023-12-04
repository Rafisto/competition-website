package org.contesthub.apiserver.databaseInterface.models;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "contest_problems")
public class ContestProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", length = Integer.MAX_VALUE)
    private String title;

    @Column(name = "contents", length = Integer.MAX_VALUE)
    private String contents;

    @Column(name = "use_autograding")
    private Boolean useAutograding;

    @Column(name = "use_autograding_answer", length = Integer.MAX_VALUE)
    private String useAutogradingAnswer;

    @Column(name = "deadline")
    private Instant deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    private Contest contest;

    public ContestProblem() {
    }

    public ContestProblem(String title, String contents, Boolean useAutograding, String useAutogradingAnswer, Instant deadline, Contest contest) {
        this.title = title;
        this.contents = contents;
        this.useAutograding = useAutograding;
        this.useAutogradingAnswer = useAutogradingAnswer;
        this.deadline = deadline;
        this.contest = contest;
    }

    @OneToMany
    @JoinColumn(name="problem_id")
    private Set<ContestGrading> contestGradings = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Boolean getUseAutograding() {
        return useAutograding;
    }

    public void setUseAutograding(Boolean useAutograding) {
        this.useAutograding = useAutograding;
    }

    public String getUseAutogradingAnswer() {
        return useAutogradingAnswer;
    }

    public void setUseAutogradingAnswer(String useAutogradingAnswer) {
        this.useAutogradingAnswer = useAutogradingAnswer;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public void setDeadline(Instant deadline) {
        this.deadline = deadline;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public Set<ContestGrading> getContestGradings() {
        return contestGradings;
    }

    public void setContestGradings(Set<ContestGrading> contestGradings) {
        this.contestGradings = contestGradings;
    }

}