package com.example.application.views.corerealm;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;

/**
 * A Designer generated component for the person-form-view template.
 *
 * Designer will add and remove fields with @Id mappings but does not overwrite
 * or otherwise change this file.
 */
@JsModule("./views/corerealm/card-list-item.ts")
@Tag("card-list-item")
public class CardListItem extends LitTemplate {

    @Id("image")
    private Image image;
    @Id("name")
    private Span name;
    @Id("date")
    private Span date;
    @Id("post")
    private Span post;
    @Id("likes")
    private Span likes;
    @Id("comments")
    private Span comments;
    @Id("shares")
    private Span shares;

    public CardListItem(Person person) {
        image.setSrc(person.getImage());
        name.setText(person.getName());
        date.setText(person.getDate());
        post.setText(person.getPost());
        likes.setText(person.getLikes());
        comments.setText(person.getComments());
        shares.setText(person.getShares());
    }
}
