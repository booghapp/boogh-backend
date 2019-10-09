export const enum DocType {
    ABOUT = 'ABOUT',
    TERMSOFUSE = 'TERMSOFUSE',
    PRIVACYPOLICY = 'PRIVACYPOLICY'
}

export interface IDocument {
    id?: number;
    type?: DocType;
    content?: any;
}

export const defaultValue: Readonly<IDocument> = {};
