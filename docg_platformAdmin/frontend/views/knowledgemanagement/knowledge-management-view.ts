import { html, LitElement, customElement } from 'lit-element';
import '@vaadin/vaadin-split-layout';
import '@vaadin/vaadin-grid';
import '@vaadin/vaadin-grid/vaadin-grid-column';
import '@vaadin/vaadin-form-layout';
import '@vaadin/vaadin-text-field';
import '@vaadin/vaadin-date-picker';
import '@vaadin/vaadin-date-time-picker';
import '@vaadin/vaadin-button';
import '@vaadin/vaadin-ordered-layout/vaadin-horizontal-layout';



@customElement('knowledge-management-view')
export class KnowledgeManagementView extends LitElement {
  createRenderRoot() {
    // Do not use a shadow root
    return this;
  }

  render() {
    return html`<vaadin-split-layout class="w-full h-full">
      <div class="flex-grow w-full" id="grid-wrapper">
        <vaadin-grid id="grid"></vaadin-grid>
      </div>
      <div class="flex flex-col" style="width: 400px;">
        <div class="p-l flex-grow">
          <vaadin-form-layout>
            <vaadin-text-field label="First name" id="firstName"></vaadin-text-field><vaadin-text-field label="Last name" id="lastName"></vaadin-text-field><vaadin-text-field label="Email" id="email"></vaadin-text-field><vaadin-text-field label="Phone" id="phone"></vaadin-text-field><vaadin-date-picker label="Date of birth" id="dateOfBirth"></vaadin-date-picker><vaadin-text-field label="Occupation" id="occupation"></vaadin-text-field><vaadin-checkbox id="important" style="padding-top: var(--lumo-space-m);"
            >Important</vaadin-checkbox
          >
          </vaadin-form-layout>
        </div>
        <vaadin-horizontal-layout class="w-full flex-wrap bg-contrast-5 py-s px-l" theme="spacing">
          <vaadin-button theme="primary" id="save">Save</vaadin-button>
          <vaadin-button theme="tertiary" slot="" id="cancel">Cancel</vaadin-button>
        </vaadin-horizontal-layout>
      </div>
    </vaadin-split-layout>`;
  }
}
