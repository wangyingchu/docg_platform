import { html, LitElement, customElement } from 'lit-element';
import '@vaadin/vaadin-grid';



@customElement('core-realm-view')
export class CoreRealmView extends LitElement {
  createRenderRoot() {
    // Do not use a shadow root
    return this;
  }

  render() {
    return html`<vaadin-grid id="grid" theme="no-border no-row-borders"> </vaadin-grid>`;
  }
}
