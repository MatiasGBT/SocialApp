import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class TranslateExtensionService {

  constructor(private translate: TranslateService) { }

  public getTranslatedStringByUrl(url: string): string {
    let text: string;
    this.translate.get(url).subscribe((responseText) => text = responseText);
    return text;
  }
}
