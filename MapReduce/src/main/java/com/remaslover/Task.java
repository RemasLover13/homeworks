package com.remaslover;

import java.util.Objects;

public class Task {

    private int id;
    private int numberReduce;
    private TaskType taskType;
    private String file;
    private int numberMapTasks;

    public Task(int id, int numberReduce, TaskType taskType, String file, int numberMapTasks) {
        this.id = id;
        this.numberReduce = numberReduce;
        this.taskType = taskType;
        this.file = file;
        this.numberMapTasks = numberMapTasks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumberReduce() {
        return numberReduce;
    }

    public void setNumberReduce(int numberReduce) {
        this.numberReduce = numberReduce;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public int getNumberMapTasks() {
        return numberMapTasks;
    }

    public void setNumberMapTasks(int numberMapTasks) {
        this.numberMapTasks = numberMapTasks;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Task task)) return false;
        return id == task.id && numberReduce == task.numberReduce && taskType == task.taskType && Objects.equals(file, task.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numberReduce, taskType, file);
    }

    @Override
    public String toString() {
        return "Task{" +
               "id=" + id +
               ", numberReduce=" + numberReduce +
               ", taskType=" + taskType +
               ", file='" + file + '\'' +
               ", numberMapTasks=" + numberMapTasks +
               '}';
    }
}
