import { html, LitElement, customElement } from 'lit-element';
import '@polymer/iron-icon/iron-icon';
import '@vaadin/vaadin-button';
import '@vaadin/vaadin-checkbox';
import '@vaadin/vaadin-combo-box';
import '@vaadin/vaadin-icons';
import '@vaadin/vaadin-select';
import '@vaadin/vaadin-text-field/vaadin-email-field';
import '@vaadin/vaadin-text-field/vaadin-text-area';
import '@vaadin/vaadin-text-field/vaadin-text-field';



@customElement('general-information-view')
export class GeneralInformationView extends LitElement {
  createRenderRoot() {
    // Do not use a shadow root
    return this;
  }

  render() {
    return html`
      <main class="grid gap-xl items-start justify-center max-w-screen-md mx-auto pb-l px-l">
        <section class="flex flex-col flex-grow">
          <h2 class="mb-0 mt-xl text-3xl">Checkout</h2>
          <p class="mb-xl mt-0 text-secondary">All fields are required unless otherwise noted</p>
          <section class="flex flex-col mb-xl mt-m">
            <p class="m-0 text-s text-secondary">Checkout 1/3</p>
            <h3 class="mb-m mt-s text-2xl">Personal details</h3>
            <vaadin-text-field class="mb-s" label="Name" pattern="[\\p{L} \\-]+" required></vaadin-text-field>
            <vaadin-email-field class="mb-s" label="Email address" required></vaadin-email-field>
            <vaadin-text-field class="mb-s" label="Phone number" pattern="[\\d \\-\\+]+" required></vaadin-text-field>
            <vaadin-checkbox class="mt-s">Remember personal details for next time</vaadin-checkbox>
          </section>
          <section class="flex flex-col mb-xl mt-m">
            <p class="m-0 text-s text-secondary">Checkout 2/3</p>
            <h3 class="mb-m mt-s text-2xl">Shipping address</h3>
            <vaadin-combo-box class="mb-s" id="countrySelect" label="Country" required></vaadin-combo-box>
            <vaadin-text-area
              class="mb-s"
              id="address"
              label="Street address"
              maxlength="200"
              required
            ></vaadin-text-area>
            <div class="flex flex-wrap gap-m">
              <vaadin-text-field
                class="mb-s"
                label="Postal code"
                maxlength="10"
                pattern="[\\d \\p{L}]*"
                required
              ></vaadin-text-field>
              <vaadin-text-field class="flex-grow mb-s" label="City" required></vaadin-text-field>
            </div>
            <vaadin-combo-box
              allow-custom-value
              class="mb-s"
              id="stateSelect"
              label="State"
              required
            ></vaadin-combo-box>
            <vaadin-checkbox class="mt-s">Billing address is the same as shipping address</vaadin-checkbox>
            <vaadin-checkbox>Remember address for next time</vaadin-checkbox>
          </section>
          <section class="flex flex-col mb-xl mt-m">
            <p class="m-0 text-s text-secondary">Checkout 3/3</p>
            <h3 class="mb-m mt-s text-2xl">Payment information</h3>
            <vaadin-text-field
              class="self-stretch"
              label="Cardholder name"
              pattern="[\\p{L} \\-]+"
              required
            ></vaadin-text-field>
            <div class="flex flex-wrap gap-m">
              <vaadin-text-field
                class="flex-grow"
                label="Card number"
                pattern="[\\d ]{12,23}"
                required
              ></vaadin-text-field>
              <vaadin-text-field label="Security code" pattern="[0-9]{3,4}" required>
                <span class="" slot="helper">What is this?</span>
              </vaadin-text-field>
            </div>
            <div class="flex flex-wrap gap-m">
              <vaadin-select label="Expiration month" required>
                <template>
                  <vaadin-list-box>
                    <vaadin-item>01</vaadin-item>
                    <vaadin-item>02</vaadin-item>
                    <vaadin-item>03</vaadin-item>
                    <vaadin-item>04</vaadin-item>
                    <vaadin-item>05</vaadin-item>
                    <vaadin-item>06</vaadin-item>
                    <vaadin-item>07</vaadin-item>
                    <vaadin-item>08</vaadin-item>
                    <vaadin-item>09</vaadin-item>
                    <vaadin-item>10</vaadin-item>
                    <vaadin-item>11</vaadin-item>
                    <vaadin-item>12</vaadin-item>
                  </vaadin-list-box>
                </template>
              </vaadin-select>
              <vaadin-select label="Expiration year" required>
                <template>
                  <vaadin-list-box>
                    <vaadin-item>21</vaadin-item>
                    <vaadin-item>22</vaadin-item>
                    <vaadin-item>23</vaadin-item>
                    <vaadin-item>24</vaadin-item>
                    <vaadin-item>25</vaadin-item>
                  </vaadin-list-box>
                </template>
              </vaadin-select>
            </div>
          </section>
          <hr class="mb-xs mt-s mx-0" />
          <footer class="flex items-center justify-between my-m">
            <vaadin-button theme="tertiary-inline">Cancel order</vaadin-button>
            <vaadin-button theme="primary success">
              <iron-icon icon="vaadin:lock" slot="prefix"></iron-icon>
              Pay securely
            </vaadin-button>
          </footer>
        </section>
        <aside class="bg-contrast-5 box-border p-l rounded-l sticky">
          <header class="flex items-center justify-between mb-m">
            <h3 class="m-0">Order</h3>
            <vaadin-button theme="tertiary-inline">Edit</vaadin-button>
          </header>
          <ul class="list-none m-0 p-0 spacing-b-m">
            <li class="flex justify-between">
              <div class="flex flex-col">
                <span>Vanilla cracker</span>
                <span class="text-s text-secondary">With wholemeal flour</span>
              </div>
              <span>$7.00</span>
            </li>
            <li class="flex justify-between">
              <div class="flex flex-col">
                <span>Vanilla blueberry cake</span>
                <span class="text-s text-secondary">With blueberry jam</span>
              </div>
              <span>$8.00</span>
            </li>
            <li class="flex justify-between">
              <div class="flex flex-col">
                <span>Vanilla pastry</span>
                <span class="text-s text-secondary">With wholemeal flour</span>
              </div>
              <span>$5.00</span>
            </li>
            <li class="flex justify-between">
              <div class="flex flex-col">
                <span>Blueberry cheese cake</span>
                <span class="text-s text-secondary">With blueberry jam</span>
              </div>
              <span>$7.00</span>
            </li>
          </ul>
        </aside>
      </main>
    `;
  }
}
