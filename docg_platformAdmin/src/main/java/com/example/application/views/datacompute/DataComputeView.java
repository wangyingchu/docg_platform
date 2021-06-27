package com.example.application.views.datacompute;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.template.Id;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

/**
 * A Designer generated component for the person-form-view template.
 *
 * Designer will add and remove fields with @Id mappings but does not overwrite
 * or otherwise change this file.
 */
@Route(value = "data-compute", layout = MainLayout.class)
@PageTitle("Data Compute")
@Tag("data-compute-view")
@JsModule("./views/datacompute/data-compute-view.ts")
public class DataComputeView extends LitTemplate {

    @Id("creditCardNumber")
    private TextField creditCardNumber;
    @Id("cardholderName")
    private TextField cardholderName;
    @Id("csc")
    private PasswordField csc;
    @Id("expirationMonth")
    private Select<Integer> expirationMonth;
    @Id("expirationYear")
    private Select<Integer> expirationYear;
    @Id("submit")
    private Button submit;
    @Id("cancel")
    private Button cancel;

    public DataComputeView() {
        expirationMonth.setItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        expirationYear.setItems(20, 21, 22, 23, 24, 25);

        cancel.addClickListener(e -> {
            Notification.show("Not implemented");
        });
        submit.addClickListener(e -> {
            Notification.show("Not implemented");
        });
    }

}
