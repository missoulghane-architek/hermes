import { Routes } from '@angular/router';

// Forms
import { FormElementsComponent } from './forms/form-elements/form-elements.component';

// Tables
import { BasicTablesComponent } from './tables/basic-tables/basic-tables.component';

// Charts
import { LineChartComponent } from './charts/line-chart/line-chart.component';
import { BarChartComponent } from './charts/bar-chart/bar-chart.component';

// UI Elements
import { AlertsComponent } from './ui-elements/alerts/alerts.component';
import { AvatarElementComponent } from './ui-elements/avatar-element/avatar-element.component';
import { BadgesComponent } from './ui-elements/badges/badges.component';
import { ButtonsComponent } from './ui-elements/buttons/buttons.component';
import { ImagesComponent } from './ui-elements/images/images.component';
import { VideosComponent } from './ui-elements/videos/videos.component';

// Others
import { InvoicesComponent } from './invoices/invoices.component';
import { CalenderComponent } from './calendar/calender/calender.component';
import { BlankComponent } from './blank/blank.component';

/**
 * Templates Feature Routes
 *
 * Routes pour les pages templates (forms, tables, charts, UI elements, etc.)
 * Protégées par authGuard
 */
export const TEMPLATES_ROUTES: Routes = [
  // Forms
  {
    path: 'form-elements',
    component: FormElementsComponent,
    title: 'Form Elements | Hermes'
  },

  // Tables
  {
    path: 'basic-tables',
    component: BasicTablesComponent,
    title: 'Basic Tables | Hermes'
  },

  // Charts
  {
    path: 'line-chart',
    component: LineChartComponent,
    title: 'Line Chart | Hermes'
  },
  {
    path: 'bar-chart',
    component: BarChartComponent,
    title: 'Bar Chart | Hermes'
  },

  // UI Elements
  {
    path: 'alerts',
    component: AlertsComponent,
    title: 'Alerts | Hermes'
  },
  {
    path: 'avatars',
    component: AvatarElementComponent,
    title: 'Avatars | Hermes'
  },
  {
    path: 'badge',
    component: BadgesComponent,
    title: 'Badges | Hermes'
  },
  {
    path: 'buttons',
    component: ButtonsComponent,
    title: 'Buttons | Hermes'
  },
  {
    path: 'images',
    component: ImagesComponent,
    title: 'Images | Hermes'
  },
  {
    path: 'videos',
    component: VideosComponent,
    title: 'Videos | Hermes'
  },

  // Others
  {
    path: 'invoice',
    component: InvoicesComponent,
    title: 'Invoice | Hermes'
  },
  {
    path: 'calendar',
    component: CalenderComponent,
    title: 'Calendar | Hermes'
  },
  {
    path: 'blank',
    component: BlankComponent,
    title: 'Blank Page | Hermes'
  }
];
