import { html, LitElement, customElement } from 'lit-element';
import '@vaadin/vaadin-form-layout';
import '@vaadin/vaadin-ordered-layout/vaadin-horizontal-layout';
import '@vaadin/vaadin-button';
import '@vaadin/vaadin-text-field/vaadin-password-field';
import '@vaadin/vaadin-text-field';
import '@vaadin/vaadin-custom-field';
import '@vaadin/vaadin-select';



@customElement('data-compute-view')
export class DataComputeView extends LitElement {
  createRenderRoot() {
    // Do not use a shadow root
    return this;
  }

  render() {
    return html`<h3>Credit Card</h3>
      <vaadin-form-layout style="width: 100%;">
        <vaadin-text-field
          id="creditCardNumber"
          required
          placeholder="1234 5678 9123 4567"
          label="Credit card number"
          error-message="Please enter a valid credit card number"
          pattern="[\\d ]*"
          prevent-invalid-input
        ></vaadin-text-field>
        <vaadin-text-field label="Cardholder name" id="cardholderName" colspan=""></vaadin-text-field>
        <vaadin-custom-field label="Expiration date">
          <vaadin-horizontal-layout theme="spacing">
            <vaadin-select placeholder="Month" id="expirationMonth" style="flex-grow: 1; width: 100px;"></vaadin-select>
            <vaadin-select placeholder="Year" id="expirationYear" style="flex-grow: 1; width: 100px;"></vaadin-select>
          </vaadin-horizontal-layout>
        </vaadin-custom-field>
        <vaadin-password-field
          id="csc"
          minlength="3"
          maxlength="4"
          label="CSC"
          error-message="Please enter a valid security code"
        ></vaadin-password-field>
      </vaadin-form-layout>
      <vaadin-horizontal-layout
        style="margin-top: var(--lumo-space-m); margin-bottom: var(--lumo-space-l);"
        theme="spacing"
      >
        <vaadin-button theme="primary" id="submit"> Submit </vaadin-button>
        <vaadin-button id="cancel"> Cancel </vaadin-button>
      </vaadin-horizontal-layout>`;
  }
}
