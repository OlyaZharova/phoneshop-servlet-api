package com.es.phoneshop.model.advancedSearch;

public class SearchParams {
    private String description;
    private Integer minPrice;
    private Integer maxPrice;
    private boolean choiceWord;

    public SearchParams(){
    }

    public SearchParams(String description, int minPrice, int maxPrice, boolean choiceWord) {
        this.description = description;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.choiceWord = choiceWord;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public boolean isChoiceWord() {
        return choiceWord;
    }

    public void setChoiceWord(boolean choiceWord) {
        this.choiceWord = choiceWord;
    }
}
