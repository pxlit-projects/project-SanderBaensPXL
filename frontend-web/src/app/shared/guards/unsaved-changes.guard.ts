import { CanDeactivateFn } from '@angular/router';

export interface CanComponentDeactivate {
  hasUnsavedChanges: () => boolean | null;
}

export const unsavedChangesGuard: CanDeactivateFn<CanComponentDeactivate> = (component, currentRoute, currentState, nextState) => {
  if(component.hasUnsavedChanges()){
    return window.confirm("There are unsaved changes. Are you sure you want to leave?")
  }
  return true;
};
